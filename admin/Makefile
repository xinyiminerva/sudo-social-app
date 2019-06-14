.PHONY: first_time_init init package status local run pause clean

init:
	docker run --name the-buzz -e POSTGRES_PASSWORD=sacrifice -e POSTGRES_USER=aztecs -d -p 5432:5432 postgres

status:
	docker ps -a

package:
	mvn package

local:
	DATABASE_URL=postgres://aztecs:sacrifice@localhost:5432/ mvn exec:java

run:
	DATABASE_URL=postgres://qzkgdlnwbjfnlc:39f1dedf4dde69fdb1176255466f5875a1946deaf5876d4515883fd8ef955f63@ec2-54-243-223-245.compute-1.amazonaws.com:5432/d7fnv025ml8fl6 mvn exec:java

pause:
	docker pause the-buzz

clean:
	docker stop the-buzz
	docker rm the-buzz
