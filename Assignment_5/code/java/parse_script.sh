i=1
for file in /home/vitidn/mydata/repo_git/CSCI572/Assignment_4/data/NYTimesData/NYTimesDownloadData/*.*
do
	echo "process file no.:$i"
	java -jar tika-app.jar --text $file >> parsed.txt
	i=$((i+1))
done