# Architecture design

## Front :

En front nous avons décidé d’utiliser React et non Vue.js car l’écosystème React est plus important et la technologie est un peu plus en vogue.
Nous avons utilisé [create-react-app](https://github.com/facebook/create-react-app) pour créer l’application react facilement. 
Pour effectuer nos requêtes HTTP, nous utilisons Axios. 

Si nous avions eu plus de temps nous aurions utilisé Redux pour gérer les states plus facilement au lieu de passer les données de composants en composants dans les `props`, ce qui rend la maintenance de l'application compliquée.
De plus nous aurions dû plus découper nos componsants en sous-composants, mais par manque de temps nous avons privilégié la facilité sur la modularité.
