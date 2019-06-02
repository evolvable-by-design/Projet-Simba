# Architecture design

## Front :

En front nous avons décidé d’utiliser React et non Vue.js car l’écosystème React est plus important et la technologie est un peu plus en vogue.
Nous avons utilisé [create-react-app](https://github.com/facebook/create-react-app) pour créer l’application react facilement. 
Pour effectuer nos requêtes HTTP, nous utilisons Axios. 

Si nous avions eu plus de temps nous aurions utilisé Redux pour gérer les states plus facilement au lieu de passer les données de composants en composants dans les `props`, ce qui rend la maintenance de l'application compliquée.
De plus nous aurions dû plus découper nos componsants en sous-composants, mais par manque de temps nous avons privilégié la facilité sur la modularité.

## Back :

Côté back, nous avions dans un premier temps décider d'utiliser [Quarkus](https://quarkus.io/), un nouveau framework java. Quarkus permet de compiler du code java au format binaire, au lieu de l’approche bytecode de la JVM habituelle. Il génére également des images Docker afin de faciliter le déploiement.

Toutefois, sagissant d'une nouvelle technologie avec encore peu de feedback, et compte tenu de notre innexpérience sur le framework, nous passions plus de temps à régler les problèmes de configuration (et essentiellement de déploiement) qu'à réellement coder l'application.

Nous avons donc décider de reprendre notre projet sur [Spring Boot](https://spring.io/projects/spring-boot), dû au fait que nous avions plus d'expérience sur l'utilisation de ce framework. 

Pour la base de donnée, nous avons utiliser [H2](https://www.h2database.com/html/main.html) pendant le début du développement en données non persistantes, et [MySQL](https://www.mysql.com/fr/) pour la suite en données persistantes.

Pour le pad, nous avons choisi d'utiliser [Etherpad](https://etherpad.org/) ([Github](https://github.com/ether/etherpad-lite)) pour sa simplicité d'utilisation.

Pour le système de chat, le temps commençant à nous manquer, nous avons fini par utiliser [tlk](https://tlk.io/) pour sa simplicité d'utilisation. En effet, tlk ne nécessite pas l'utilisation d'api particulière, son fonctionnement se basant sur un système de channel directement crée via l'url.


