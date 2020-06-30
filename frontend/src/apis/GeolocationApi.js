function getLocation(options) {
    return new Promise(function(resolve, reject) {
      navigator.geolocation.getCurrentPosition(resolve, reject, options);
    });
  }
  
async function getCountry(){
    try {     
      //const position = await getLocation()
      //const res = await fetch(`https://geocode.xyz/${position.coords.latitude},${position.coords.longitude}?json=1`);
      //const promapi2 = await fetch(`https://geocode.xyz/${position.coords.latitude},${position.coords.longitude}?json=1`);
      //const res = await Promise.race([promapi1,promapi2])
      const res = await fetch(`https://geocode.xyz/-34.60988,-58.45221?json=1`);
      const data = await res.json();
      console.log(data)
      return {countryIso: data.prov, countryName: data.country};
      
    } catch(error){
      console.log(error)
    }
}


  export { getLocation, getCountry };