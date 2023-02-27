## Polytechnic of Porto, School of Management and Technology (ESTG) 22/23
<a href="https://www.estg.ipp.pt/"><img src="https://user-images.githubusercontent.com/44362304/94424125-9f4d8a00-0181-11eb-84cb-174d8dbde5ec.png" title="ESTG"></a>

(Click on the above image for more school's details)

## Where2Go
![Where2Go Logo](https://user-images.githubusercontent.com/44362304/220715165-28e27f1f-54eb-47d1-a5be-1de03c22783a.png)


### School project developed using microservices approach for unit course "Software Development Support Techniques and Methods"

Where2Go is a platform where the user has the possibility to explore touristic points of different locations and in turn also has the possibility to create itineraries according to a starting point and an arrival point or explore itineraries already made by other users of the platform.
The user can explore all the tourist points that are distributed by different categories (Museums, bridges, theaters, galleries, among many others) and thus choose the attractions that suit him best to later generate his itinerary. Once the points have been chosen, the user will be presented with the most efficient route to visit the sights he has chosen.

## Table of Contents

- [Technologies](#technologies)
- [Project Architecture](#architecture)
- [Demo](#demo)
- [Project Contributors](#project_contributors)


<a name="technologies"></a>
## Technologies

| <a href="https://www.java.com/en/download/help/java8.html">`Java 8`</a> | <a href="https://spring.io/">`Java Spring Boot 2.7.6`</a> | <a href="https://www.postgresql.org/">`PostgreSQL`</a> | <a href="https://rabbitmq.com/">`RabbitMQ`</a>
| :---: |:---:|:---:|:---:|
| <img src="https://user-images.githubusercontent.com/44362304/221693853-1a4fc5ab-6593-4b9a-abee-03126ebee2ed.jpg" width="150" height="100">| <img src="https://user-images.githubusercontent.com/44362304/221695202-9f54c9aa-6f90-4af8-bb7a-900ec746ad80.png" width="150" height="100"> | <img src="https://user-images.githubusercontent.com/44362304/221695739-2404d9d5-3be1-4844-a746-cc601d41b7f7.png" width="150" height="100"> | <img src="https://user-images.githubusercontent.com/44362304/221695934-52ba8a57-eabf-47c0-abdf-567de23c5d47.png" width="150" height="100"> 
| <a href="https://konghq.com/">`Kong`</a> | <a href="https://angular.io/">`Angular 15`</a> | <a href="https://www.docker.com/">`Docker`</a> | <a href="https://kubernetes.io/">`Kubernetes`</a>
| <img src="https://user-images.githubusercontent.com/44362304/221696299-0aa01910-1d6a-408b-aa3c-4541c1fc2876.png" width="200" height="100"> | <img src="https://user-images.githubusercontent.com/44362304/221696429-ba4a76a8-53ca-4bd5-ba6f-6b520c3e2b21.png" width="170" height="100"> | <img src="https://user-images.githubusercontent.com/44362304/221696629-1ceb0906-39e5-46bc-8de9-fe0a5d573761.png" width="150" height="100"> | <img src="https://user-images.githubusercontent.com/44362304/221696763-2c9e35ca-6413-4355-a019-0adfa5afccd2.png" width="150" height="100">

<a name="architecture"></a>
## Project Architecture
<img src="https://user-images.githubusercontent.com/44362304/220716440-823ff06b-b920-4aea-b85b-d2e062a04d0f.png" width="700" height="400">

The architecture of this project is divided into 4 layers, where the Client layer is responsible for presenting information visually to the user and also for making requests to the Kong API Gateway. Next we have the API Gateway layer which will load the route information from the microservices present in Konga, receive the HTTP requests coming from the Client layer and route those requests to the correct microservices and finally send the result of those requests back to the Client. 
Then we have the External API's layer which contains the following APIs. These APIs have the function of answering the HTTP requests made by the Points Of Interest Management Service and Place Service microservices:
- <a href="https://opentripmap.io/docs#/">`OpenTripMap API`</a> - API that returns information about points of interest and also gives us the possibility of being able to do a search for existing points of interest in a given locality
- <a href="https://geocode.xyz/">`Geocode.xyz`</a> - API that returns geographic information about a given location 

In this architecture is also present the layer that contains all the microservices of the application, where they are responsible for responding to requests received by the API Gateway Kong. Finally, out of these 4 layers, 3 technologies are referenced that will be responsible for making the local deployment of the solution (Kubernetes + Docker)

<a name="demo"></a>
## Demo
Here are some of videos that shows some of the main features of this project:

### Points of Interest Search and Itinerary Generation
<img src="https://user-images.githubusercontent.com/44362304/221690060-e474a376-c5a1-49ba-a325-2f99bc8c0e59.mp4"/>


### Adding Review to a point of interest and adding a point of interest to favorites
<img src="https://user-images.githubusercontent.com/44362304/221669650-d493f55d-9ed4-49e0-99c4-8e8bf1d8f10e.mp4"/>

<a name="project_contributors"></a>
## Project Contributors
| João Pereira | Paulo da Cunha | Catarina Moreira | André Ventura
| :---: |:---:|:---:|:---:| 
| ![João Pereira](https://avatars2.githubusercontent.com/u/44362304?s=200&u=e779f8e4e1d4788360e7478a675df73f219b42b4&v=3)| ![Paulo da Cunha](https://avatars0.githubusercontent.com/u/39674226?s=200&u=5e980e380bf0b9d7a7f821ddcc6fe6112e026ae9&v=4) | ![Catarina Moreira](https://user-images.githubusercontent.com/44362304/220717116-da65bdbc-1fbd-41c2-863f-c8bba33a5f34.png) | ![André Ventura](https://user-images.githubusercontent.com/44362304/220717113-2cae8ad8-fa0a-40c9-8f52-24e1f1cd5689.png) 
| <a href="https://github.com/Joaodevgit" target="_blank">`github.com/Joaodevgit`</a> | <a href="https://github.com/PauloDevGit" target="_blank">`github.com/PauloDevGit`</a> | <a href="https://github.com/CatarinaMoreira29" target="_blank">`github.com/CatarinaMoreira29`</a> | ---
