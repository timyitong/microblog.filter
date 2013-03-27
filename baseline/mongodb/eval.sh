mv ../../data/result/13* ../../data/result/$1
cp ../../data/result/$1 ../../data/result/original/$1
gzip -9 ../../data/result/$1
python ../../tools/eval/mb12-filteval.py ../../data/train.topics.filtering.txt ../../data/train.filtering-qrels.txt ../../data/result/$1.gz > ../../data/result/$1.eval
rm ../../data/result/*.gz
