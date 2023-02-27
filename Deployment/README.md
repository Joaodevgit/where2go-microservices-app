On this section you can encounter some detailed information and guides on how to create a docker image and how to test locally kubernetes project manifest yml files using minikube tool.

## Table of Contents

- [Guide of how to build microservices into docker image](#guide_1)
- [How to deploy locally the application using kubernetes minikube tool](#guide_2)

<a name="guide_1"></a>
## Guide of how to build microservices into docker image

### Using the spring boot maven plugin:

#### 1 - Add the spring-boot-maven-plugin to the microservice pom.xml file: (Ensure the <artifactId> tag is present in the pom.xml file)

![Step_1](https://user-images.githubusercontent.com/44362304/219944085-15046a0e-c198-4138-bdd1-907d634f65d4.png)

#### 2 - Run the command ```mvn spring-boot:build-image``` (in the path of the microservice pom.xml)

#### 3 - (Optional) - Add a tag to the created microservice image by running the following command: ```docker tag <old_microservice_tag> <new_microservice_tag>```

#### 4 - Push the generated microservice image to the dockerhub repository by running the following command: ```docker push <dockerhub_repository_name>:<microservice_image_tag>```

#### Prerequisites
- Have the docker engine installed on the machine (Installation guide: https://docs.docker.com/engine/install/)
- Have the maven command installed on the machine (Installation Guide: https://stackoverflow.com/questions/19090928/mvn-command-is-not-recognized-as-an-internal-or-external-command)

### Without using the spring boot maven plugin:

#### 1 - Create the Dockerfile for the microservice

#### 2 - Run the docker command ```build -t <microservice_image_name>:<microservice_image_tag> . ``` that will run the microservice Dockerfile and will convert it into a Docker image

#### 3 - (Optional) - Add a tag to the created microservice image by running the following command: ```docker tag <old_microservice_tag> <new_microservice_tag>```

#### 4 - Push the generated microservice image to the dockerhub repository by running the following command: ```docker push <dockerhub_repository_name>:<microservice_image_tag>```

#### Prerequisites
- Have the docker engine installed on the machine (Installation guide: https://docs.docker.com/engine/install/)

<a name="guide_2"></a>

## How to deploy locally the application using kubernetes (<a href="https://minikube.sigs.k8s.io/docs/" target="_blank">`minikube`</a>)

### Creating a local Kubernetes cluster using minikube

#### 1 - Run the command ```minikube start```

![Minikube-Start](https://user-images.githubusercontent.com/44362304/219949231-769285ea-22f0-4c82-b024-0e36e04ded60.png)d 

#### 2 - Run the command ```minikube dashboard ```, to get more visual information about the services, deployments, etc., that are being created during the development of the application

![Minikube-Dashboard](https://user-images.githubusercontent.com/44362304/219949226-410b6666-0d64-4ae4-b024-515770cf46e7.png)

![Minikube-Dashboard-Overview](https://user-images.githubusercontent.com/44362304/219951927-28593176-1d86-4a33-8b17-d8909185885a.png)

#### 3 - As the application developed uses the Gateway API <a href="https://konghq.com/products/kong-gateway" target="_blank">`Kong`</a> one must run the following commands to get kong installed on the kubernetes cluster

- ```
  helm repo add kong https://charts.konghq.com
- ```
  helm repo update
- ```
  helm install kong/kong --set admin.enabled=true --set admin.http.enabled=true --set 
  postgresql.enabled=true   --set postgresql.postgresqlUsername=kong  --set 
  postgresql.postgresqlDatabase=kong --set env.database=postgres --generate-name

#### 4 - Run the command ```minikube ip``` to find out the minikube ip

#### 5 - Run the ```kubectl apply -f konga-manifest.yml``` command on the path where the konga yml file is located ( <a href="https://github.com/Joaodevgit/where2go-microservices-app/tree/main/Deployment">Konga-manifest.yml file path</a> )

#### 6 - Run the command ```kubectl get services``` to find out the port of the service (konga-svc) to be able to access the <a href="https://pantsel.github.io/konga/" target="_blank">`Konga`</a> interface:

![Konga-Port](https://user-images.githubusercontent.com/44362304/219950156-0bbd82b9-fdb2-4547-8253-7821d357a069.png)

#### 7 - After filling in the Konga url (<minikube_ip>:<konga_port>) and adding the connection as illustrated below, where the format of the kong admin url (kong-1676747057-kong-admin) is <minikube_ip>:<kong_admin_port>:

![Minikube-Dashboard](https://user-images.githubusercontent.com/44362304/219949230-73c460bf-2aa4-44dd-9e61-999f00cd696e.png)

![Konga-Connection](https://user-images.githubusercontent.com/44362304/219951228-77469724-f825-4cf0-bd92-245df3fcb572.png)

#### 8 - Import the kong snapshot containing the microservices to be registered in the Kong API Gateway <a href="https://github.com/Joaodevgit/where2go-microservices-app/blob/main/konga_snapshot.json" target="_blank">`Snapshot file`</a>: 

![Konga-Snapshot-Import](https://user-images.githubusercontent.com/44362304/219950155-e6338ad1-5eda-418f-adb9-e0cddc58d0b6.png)

#### 9 - Import 1st - Services, 2nd - Routes and 3rd - Plugins:
<img src="https://user-images.githubusercontent.com/44362304/219950153-94c51434-72f3-4414-a51a-8daaac89ddd0.png" width="100%" height="300">
<img src="https://user-images.githubusercontent.com/44362304/219950157-e1e4bb87-904d-4531-ab83-8e2c59d0888a.png" width="100%" height="300">
<img src="https://user-images.githubusercontent.com/44362304/219950158-aa822d41-4082-4253-a6a8-8c7197948cc1.png" width="100%" height="300">

#### 10 - Run the command ```kubectl get pods``` to find out the port of the **kong-proxy** pod, which is the port responsible for communicating with the Kong API to communicate with the endpoints of the microservices present in the imported snapshot:

![Kong-Proxy-Pod](https://user-images.githubusercontent.com/44362304/219951833-7d76a2f6-e3af-411a-8c93-a39fba6e4b93.png)

#### 11 - Run the command ```kubectl port-forward <pod_name_kong> 8000:8000``` to expose the kong service (api gateway) to the outside on port 8000:

![Kong-Port-Exposed](https://user-images.githubusercontent.com/44362304/219951758-58981b96-7ffa-4b73-9ac4-ca3f5e6f5fbf.png)

#### 12 - Access the url http://localhost:8000 and test one of the snapshot endpoints
