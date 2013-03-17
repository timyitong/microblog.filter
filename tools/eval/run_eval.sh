cd ../../baseline/mongodb
java -jar dist/lib/yitongz.mongodb.jar
cd ../../tools/eval
mv ../../data/result/13* ../../data/result/$1
cp ../../data/result/$1 ../../data/result/original/$1
gzip -9 ../../data/result/$1
python mb12-filteval.py ../../data/2012.topics.MB1-50.filtering.txt ../../data/filtering-qrels.txt ../../data/result/$1.gz > ../../data/result/$1.eval
rm ../../data/result/*.gz
