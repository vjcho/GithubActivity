#/bin/bash

# Script to do hot swapping

function killitif {
    docker ps -a  > /tmp/yy_xx$$
    if grep --quiet $1 /tmp/yy_xx$$
     then
     echo "killing older version of $1"
     docker rm -f `docker ps -a | grep $1  | sed -e 's: .*$::'`
   fi
}

if [ $# -eq 0 ]; then
	echo "No container name provided"
	exit 1
fi

docker=$1
old=""
new=""
script=""

# Kill any existing $docker process
# killitif web2

# Get name of live container
liveContainers=$(docker ps --format "{{.Names}}")
for name in $liveContainers
do
	if [[ $name == *"web1"* ]]; then
		old=$name
		new="web2"
		script="/bin/swap2.sh"
	elif [[ $name == *"web2"* ]]; then
		old=$name
		new="web1"
		script="/bin/swap1.sh"
	fi
done

# Bring up new container
docker run --network ecs189_default --name $new $docker &

# Redirect nginx
sleep 20
docker exec ecs189_proxy_1 /bin/bash $script

# Kill old container
sleep 1
killitif $old
