:: Maven clean-up & package
call mvn clean
call mvn package

:: perform tests
cd src/test
py.test --json=../../target/test/report.json --html=../../target/test/report.html
python grade.py --tests-file ./alltests --report-file ../../target/test/report.json > ../../target/test/grade.txt

:: back to root
cd ../../