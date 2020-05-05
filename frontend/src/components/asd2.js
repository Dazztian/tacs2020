Enrutando en React (React Router v4)
Simon Hoyos
Simon Hoyos
Follow
Feb 21, 2018 · 6 min read



Hoy veremos una librería que para crear rutas en nuestra aplicación. Esta librería es react-router. Cuenta con 2 subdivisiones, una para la web (react-router-dom) y otra para desarrollo móvil (react-router-native).
Hoy nos enfocaremos en la parte web de esta librería, que actualmente se encuentra en la versión 4.2.2. Para estas nuevas versiones (v4), react-router cambia un poco su filosofía, haciendo diferenciación entre el enrutamiento estático que usaban anteriormente y el enrutamiento dinámico actual.
¿Qué es enrutamiento estático?
Este es el enrutamiento más común, si alguna vez hemos trabajado con rutas en algún otro lenguaje de programación o con algún otro framework, seguramente lo habremos hecho usando enrutamiento estático.
En este, las rutas son definidas al momento en que nuestra aplicación es inicializada. Es decir, antes que nuestra aplicación se renderice.
¿Qué es enrutamiento dinámico?
Este es el tipo de enrutamiento usado por react-router. A diferencia de el enrutamiento estático, este toma lugar en el momento en que nuestra aplicación se está renderizando. Esto gracias a que react-router hace uso de componentes para definir sus rutas.
Los componentes que se encargan de mostrar las diferentes rutas siempre renderizan. Algunas veces renderizan un componente y otras veces null, todo dependiendo de la locación.
Componentes
Ahora que sabemos que React Router utiliza componentes, miremos algunos de ellos y que podemos hacer a través de algunos ejemplos:
BrowserRouter
Es una envoltura para nuestra aplicación. Esta envoltura nos da acceso al API de historia de HTML5 para mantener nuestra interfaz gráfica en sincronía con la locación actual o URL. Debemos tener en cuenta que esta envoltura solo puede tener un hijo. Por lo general es Switch.
Switch
Este componente, causa que solo se renderice el primer hijo Route o Redirect que coincida con la locación o URL actual.
En el caso que no usemos Switch todas las rutas que cumplan con la condición se renderizarán.
Route
Para definir las diferentes rutas de nuestra aplicación, podemos usar el componente Route. La función de este componente es elegir que renderizar según la locación actual. Este es el caso que vimos anteriormente, todos los componentes Route siempre renderizan, pero algunas veces renderizan un componente y otras veces retornan null.
import React, { Component } from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
export default class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <Switch>
          <Route path='/' exact />
          <Route path='/index' exact />
          <Route path='/post/:id' />
        </Switch>
      </BrowserRouter>
    );
  }
}
en caso que la locación actual sea www.dominio.com/index se mostrara la segunda ruta en el ejemplo anterior, aunque en este caso no le estamos asignando ningún componente a renderizar cuando esto suceda.
Este componente recibe las siguientes propiedades:
path: la ruta en la que se renderizará el componente en forma de cadena de texto
exact: un booleano para definir si queremos que la ruta tiene o no que ser exacta para renderizar un componente. Eg: /index !== /index/all.
strict: un booleano para definir si queremos que el último slash sea tomado en cuenta para renderizar un componente. Eg: /index !== /index/.
sensitive: un booleano para definir si queremos distinguir entre minúsculas y mayúsculas, y tomar esto en cuenta para renderizar un componente. Eg: /index !== /Index
component: recibe un componente a renderizar. Crea un nuevo elemento de React cada vez. Esto causa que el componente se monte y desmonte cada vez (no actualiza).
render: recibe un método que retorna un componente. A diferencia de component no remonta el componente.
children: funciona muy similar a render, pero con algunas adiciones. Hoy no voy a ahondar mucho en está propiedad. Con los 2 métodos de renderizando anteriores sera suficiente para lo que queremos hacer.
Los 3 métodos para renderizar el componente (propiedades de Route) nos permiten acceder a 3 props adicionales. Estos 3 props contienen la data del API de historia de HTML5 y estos son match, location, history.
En caso que queramos acceder a estas propiedades desde un componente que no fue renderizado de ninguna de estas maneras React Router nos brinda un componente de alto orden (HOC = High Order Component) para darnos ese acceso. Este componente es withRouter.
En el pasado tuve la oportunidad de hacer una charla de React Router en el Meetup React Medellin. El ejemplo que compartiré el día de hoy, es precisamente el repositorio de esa charla. Al correr el proyecto si navegan a http://localhost:3000/page2 (o cualquier puerto en el que estén corriendo local) y abren la consola del navegador podrán ver los valores que incluyen cada una de estas propiedades.
import React, { Component } from 'react';
import { BrowserRouter, Route, Switch, Redirect } from 'react-router-dom';
import NavBar from './NavBar/NavBar';
import Home from './Home/Home';
import Page1 from './Page1/Page1';
import Page2 from './Page2/Page2';
import PageError from './PageError/PageError';
import './App.css';
class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <div>
          <NavBar />
          <Redirect
            from="/"
            to="/home" />
          <Switch>
            <Route
              path="/home"
              component={Home} />
            <Route
              exact
              path="/page1"
              render={() => <Page1 name="React Medellín" />} />
            <Route
              exact
              path="/page2"
              render={() => <Page2 />} />
            <Route component={PageError} />
          </Switch>
        </div>
      </BrowserRouter>
    );
  }
}
export default App;
Este ejemplo renderizará los componentes de la siguiente manera:
si la ruta es /home renderizará el componente Home.
si la ruta es exactamente /page1 renderiza el componente Page1.
si la ruta ese exactamente /page2 renderiza el componente Page2.
y si no es ninguna de las anteriores se renderizará el componente PageError para mostrar que ingresamos a una ruta no valida.
Redirect
Podemos ver que en el ejemplo anterior usamos, además de los componentes que ya vimos, el componente Redirect. Este componente causa un redireccionamiento a una ruta diferente a la actual. La ruta a la que este nos redirecciona reemplaza la locación actual en la historia del navegador.
Redirect recibe las siguientes propiedades:
from: una locación en forma de cadena de texto u objeto desde la cual se va a hacer el redireccionamiento. Esta propiedad funciona de manera muy similar a la propiedad path de Route, cuando la locación coincide con from, se ejecuta el Redirect.
to: una locación en forma de cadena de texto u objeto hacía el cual se va a hacer el redireccionamiento.
push: un booleano que nos permite modificar el comportamiento de Redirect. Cuando este es verdadero en lugar de reemplazar la locación actual en la historia, este agrega una nueva locación.
Al igual que Route, podemos usar las propiedades strict, y exact.
En el ejemplo anterior si navegamos al index de la aplicación /, Redirect cambiara esa locación por /home. Y además renderizará el componente Route que coincida con esta ruta, en este caso renderiza Home.
Link
Ya hemos configurado nuestras rutas, pero aún no tenemos forma de navegar por nuestra aplicación. Si queremos cambiar de un componente a otro nos tocaría navegar cambiando la URL de manera manual.
Link crea un hipervínculo que nos permite navegar por nuestra aplicación. Al contrario de Redirect, este agrega una nueva locación a la historia.
Recibe las siguientes propiedades:
to: una locación en forma de cadena de text. Esta será la locación a la que navegaremos cuando le damos click a un hipervínculos.
replace: un booleano que nos permite modificar el comportamiento de Link. Cuando este es verdadero en lugar de agregar una nueva locación a la historia, este reemplaza la locación actual.
import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import './NavBar.css';
class NavBar extends Component {
  render() {
    return (
      <div className="NavBar">
        <div className="link-container">
          <Link to="/page1" className="link">Página 1</Link>
        </div>
        <div className="link-container">
          <Link to="/page2" className="link">Página 2</Link>
        </div>
      </div>
    );
  }
}
export default NavBar;
Código fuente: GitHub
En Resumen
React Router nos provee todos los componentes necesarios para hacer que nuestra SPA (single page application) se mantenga en sincronía con nuestra locación actual.
BrowserRouter, Route, Switch, Redirect, y Link, son los componentes más básicos y obligatorios, y son solo algunos de los componentes que tenemos a nuestra disposición a la hora de trabajar con rutas en React (visita la documentación oficial de react-router para ver más: React Router).
204

React
React Medellin
Tutorial
Spanish
React Router