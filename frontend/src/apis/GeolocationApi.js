
function getLocation(options) {
    return new Promise(function(resolve, reject) {
      navigator.geolocation.getCurrentPosition(resolve, reject, options);
    });
  }
  
async function getCountry(){
    try {
      const position = await getLocation()
      return new Promise(resolve => {
        setTimeout(() => {
          resolve({countryIso: 'AR', countryName: 'Argentina'});
        }, 1000);
      });
      
      /*const position = await getLocation()
      const promapi1 = await fetch(`https://geocode.xyz/${position.coords.latitude},${position.coords.longitude}?json=1`);
      //const promapi2 = await fetch(`https://geocode.xyz/${position.coords.latitude},${position.coords.longitude}?json=1`);
      //const res = await Promise.race([promapi1,promapi2])
      const data = await response.json();
      return {countryIso: data.prov, countryName: data.country};*/ //prov seria el pais ISO
      
    } catch(error){
      console.log(error)
    }
}


  export { getLocation, getCountry };