# Define all internal variables:
CONTAINER=the-buzz
PASSWORD=sacrifice
USER=aztecs

# Define all targets:
.PHONY: package init status run publish clean

# Package the application:
package:
	mvn package -e

# Initialize the docker container:
init:
	sudo docker run -p 5432:5432 --name $(CONTAINER) -e POSTGRES_PASSWORD=$(PASSWORD) -e POSTGRES_USER=$(USER) -d postgres

# Get the status of the docker container:
status:
	docker stats $(CONTAINER)

# Run the server:
run:
	DATABASE_URL=postgres://$(USER):$(PASSWORD)@127.0.0.1:5432/ MEMCACHIER_PASSWORD=ECB5B3769353DFC4284D0A9902D618B0 MEMCACHIER_SERVERS=mc3.dev.ec2.memcachier.com:11211 MEMCACHIER_USERNAME=40F166 mvn exec:java

# Publish the results to Heroku:
publish: package
	mvn heroku:deploy

# Remove the container:
clean:
	sudo docker stop $(CONTAINER)
	sudo docker rm $(CONTAINER)
