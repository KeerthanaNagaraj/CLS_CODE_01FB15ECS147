#cd zookeeper-3.4.11
javac -cp zookeeper-3.4.11.jar:json-simple-1.1.1.jar:log4j-1.2.16.jar:slf4j-api-1.6.1.jar:slf4j-log4j12-1.6.1.jar  ZooKeeperConnection.java
javac -cp zookeeper-3.4.11.jar:json-simple-1.1.1.jar:log4j-1.2.16.jar:slf4j-api-1.6.1.jar:slf4j-log4j12-1.6.1.jar  ZKExists.java
java -cp .:.zookeeper-3.4.11.jar:json-simple-1.1.1.jar:log4j-1.2.16.jar:slf4j-api-1.6.1.jar:slf4j-log4j12-1.6.1.jar ZKExists > exists.txt
javac -cp json-simple-1.1.1.jar server.java
var=$(tail -n1 exists.txt)
if [ "$var" = 0 ]; then
	echo "INITIALIZING ...."
	javac -cp zookeeper-3.4.11.jar:json-simple-1.1.1.jar:log4j-1.2.16.jar:slf4j-api-1.6.1.jar:slf4j-log4j12-1.6.1.jar  ZKCreate.java
	java  -cp .:.zookeeper-3.4.11.jar:json-simple-1.1.1.jar:log4j-1.2.16.jar:slf4j-api-1.6.1.jar:slf4j-log4j12-1.6.1.jar  ZKCreate
	var=$(tail -n1 META_ip.txt)
	echo $var>master_ip.txt
	(gnome-terminal --command "java server $var true" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	java -cp .:.*.jar ZKCreate1
	var=$(tail -n2 META_ip.txt | head -n1)
	(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	var=$(tail -n1 META_ip.txt)
	(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	javac -cp zookeeper-3.4.11.jar:*.jar ZKCreate2.java
	java -cp .:.*.jar ZKCreate2
	var=$(tail -n2 META_ip.txt | head -n1)
	(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	var=$(tail -n1 META_ip.txt)
	(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	echo "MASTER CREATED"
else
	echo "MASTER EXISTS"
	head -n1 META.txt > temp.txt
	rm META.txt
	mv temp.txt META.txt
	var=$(tail -n1 master_ip.txt)
		(gnome-terminal --command "java server $var true" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	javac -cp zookeeper-3.4.11.jar:*.jar ZKCreate1.java
	java -cp .:.*.jar ZKCreate1
	var=$(tail -n2 META_ip.txt | head -n1)
		(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	var=$(tail -n1 META_ip.txt)
		(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	javac -cp zookeeper-3.4.11.jar:*.jar ZKCreate2.java
	java -cp .:.*.jar ZKCreate2
	var=$(tail -n2 META_ip.txt | head -n1)
		(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	var=$(tail -n1 META_ip.txt)
		(gnome-terminal --command "java server $var false" &) && sleep 0.8 && xdotool windowminimize $(xdotool search --class 'gnome-terminal' |sort|tail -1)
	
fi
echo "INITIALIZING CLIENT ...."
var=$(tail -n1 master_ip.txt)
num_var=$(wc -l < META.txt)
javac -cp zookeeper-3.4.11.jar:*.jar client.java
java -cp .:.*.jar client $var 6000 $num_var






