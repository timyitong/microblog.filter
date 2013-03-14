#!/usr/bin/env python2.7

import argparse
import collections
import gzip
import numpy as np
import random
import re
from warnings import warn
from bs4 import BeautifulSoup as bs
import EvalJig as ej

apar = argparse.ArgumentParser(description='Evaluation script for TREC 2012 Microblog Track filtering task runs')
apar.add_argument('topics', help='Filtering topics file')
apar.add_argument('qrels', help='Relevance judgments file')
apar.add_argument('run', help='System output file')
apar.add_argument('-v', '--verbose', action='store_false', help='Be verbose')
apar.add_argument('-l', dest='min_rel', type=int, default=1,
                  help='Minimum rel value to count as relevant (default: 1)')
args = apar.parse_args()

class FilterJig(ej.EvalJig):
    """Some changes to the EvalJig for the filtering task."""
    def __init__(self):
        ej.EvalJig.__init__(self)
    def compute(self, topic, ranking, qrel):
	"""Compute measures without truncating the run at any cutoff, and without
	sorting.  If measures need an order, they'll have to do the sorting.  So
	be careful adding EvalJig measures."""
	self.topics.add(topic)
	for op in self.ops:
	    self.score[str(op)][topic] = op.compute(ranking, qrel,
						    minrel=self.minrel)

jig = FilterJig()
jig.add_op(ej.NumRetr())
jig.add_op(ej.NumRel())
jig.add_op(ej.RelRet())

jig.minrel = args.min_rel
jig.verbose = args.verbose

class Precision(ej.Measure):
    def __init__(self):
        ej.Measure.__init__(self)
    def __str__(self): return 'set_prec'
    def compute(self, ranking, qrel, minrel):
        if len(ranking) == 0:
            return 0.0
        rel_ret = sum(1 for (s, d) in ranking if qrel.has_key(d) and qrel[d] >= minrel)
        return float(rel_ret)/len(ranking)

class Recall(ej.Measure):
    def __init__(self):
        ej.Measure.__init__(self)
    def __str__(self): return 'set_recl'
    def compute(self, ranking, qrel, minrel):
        rel_ret = sum(1 for (s, d) in ranking if qrel.has_key(d) and qrel[d] >= minrel)
        num_rel = sum(1 for rel in qrel.values() if rel >= minrel)
        return float(rel_ret)/num_rel

class Fb(ej.Measure):
    def __init__(self, beta=1):
        ej.Measure.__init__(self)
        self.beta = beta
    def __str__(self): return 'F_{}'.format(self.beta)
    def compute(self, ranking, qrel, minrel):
        if len(ranking) == 0: return 0.0
        rel_ret = sum(1 for (s, d) in ranking if qrel.has_key(d) and qrel[d] >= minrel)
        num_rel = sum(1 for rel in qrel.values() if rel >= minrel)
        prec = float(rel_ret)/len(ranking)
        recl = float(rel_ret)/num_rel
        if prec == 0.0 and recl == 0.0:
            return 0.0
        return (1 + self.beta * self.beta) * (prec * recl) / ((self.beta * self.beta * prec) + recl)

class T11SU(ej.Measure):
    def __init__(self):
        ej.Measure.__init__(self)
        self.min_u = -0.5
    def __str__(self): return 't11su'
    def compute(self, ranking, qrel, minrel):
        rel_ret = sum(1 for (s, d) in ranking if qrel.has_key(d) and qrel[d] >= minrel)
        num_rel = sum(1 for rel in qrel.values() if rel >= minrel)
        nonrel_ret = len(ranking) - rel_ret
        utility = 2.0 * rel_ret - nonrel_ret
        max_u = 2.0 * num_rel
        norm_u = utility / max_u
        scaled_u = (max(norm_u, self.min_u) - self.min_u) / (1 - self.min_u)
        return scaled_u

jig.add_op(Precision())
jig.add_op(Recall())
jig.add_op(Fb(0.5))
jig.add_op(T11SU())

# Identify query tweets from the topic file
qtweets = dict()
tops = bs(open(args.topics))
for t in tops.find_all('querytweettime'):
    tnum = t.parent.num.text.strip()
    tnum = re.search(r'0*(\d+)$', tnum).group(1)
    qtweets[tnum] = t.text.strip()

# Read in relevance judgments, dropping the querytweet
qrels = collections.defaultdict(dict)
with open(args.qrels) as qrelsfile:
    for line in qrelsfile:
        topic, _, docid, rel = line.split()
        if docid == qtweets[topic]:
            continue
        qrels[topic][docid] = int(rel)

# Read in the run, dropping the querytweet if retrieved
# Also only read in 'yes' retrievals
run = collections.defaultdict(list)
with gzip.open(args.run) as runfile:
    for line in runfile:
        topic, docid, score, retrieve, tag = line.split()
        if retrieve == 'no':
            continue
        topic = topic.lstrip('MB0')
        if docid == qtweets[topic]:
            continue
        run[topic].append((float(score), docid))

for topic in qrels.iterkeys():
    jig.zero(topic)
    num_rel = sum(1 for rel in qrels[topic].values() if rel >= jig.minrel)
    if num_rel == 0:
        warn("Topic "+topic+" has no relevant documents")
        continue
    if run.has_key(topic):
        jig.compute(topic, run[topic], qrels[topic])
    else:
        jig.compute(topic, [], qrels[topic])

jig.print_scores()
jig.comp_means()
jig.print_means()

