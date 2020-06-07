const axios = require('axios');

function getLocation(options) {
    return new Promise(function(resolve, reject) {
      navigator.geolocation.getCurrentPosition(resolve, reject, options);
    });
  }
  
async function getCountry(){
    try {
      return new Promise(resolve => {
        setTimeout(() => {
          resolve('El mundo del reves');
        }, 2000);
      });
  
      /*const position = await getLocation()
      const response = await fetch(`https://geocode.xyz/${position.coords.latitude},${position.coords.longitude}?json=1`);
      const data = await response.json();
      console.log(data.country);
      return data.country;*/
      
    } catch(error){
      console.log(error)
    }
}


  export { getLocation, getCountry };