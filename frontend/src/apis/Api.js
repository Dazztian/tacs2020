import { getLocation } from "./GeolocationApi"

class Api {

    constructor() {
      this.authToken = localStorage.getItem("tracker_id_token");
      this.userSessionId = localStorage.getItem("tracker_id_session");
    }
  
    headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    };
  
    BASE_URL = 'http://54.162.60.250:8080'; 

    createHeaders() {
      return !!this.authToken ? {
        ...this.headers,
        'Authorization': 'Bearer ' + this.authToken
      } : this.headers;
    }

  async getCountryList(){
    try {
        return await fetch(`${this.BASE_URL}/api/countries/names`, {
          method:'GET',
          headers: this.createHeaders(),  
        });
    } catch(error) {
      console.log(error)
    }

  }

  async getUserLists() {
    try{
      return await fetch(`${this.BASE_URL}/api/user/${this.userSessionId}/lists`, {
        method: 'GET',
        headers: this.createHeaders()
      });
    }catch(e){
      console.log(e)
    }
  }
    async getUserListsIDUser(userID) {
    try{
      return await fetch(`${this.BASE_URL}/api/user/${userID}/lists`, {
        method: 'GET',
        headers: this.createHeaders()
      });
    }catch(e){
      console.log(e)
    }
  }

  async createCountryList(nombreLista,listarray){
    try {
      return fetch( `${this.BASE_URL}/api/user/${this.userSessionId}/lists`,{
        method:"POST",
        headers: this.createHeaders(),
        body:JSON.stringify({
            "name": nombreLista,
            "countries":listarray
        })
      })
    } catch(error) {
      console.log(error)
    }
  }

  async editCountryList(name,listId,countries){
    try{
      return fetch( `${this.BASE_URL}/api/user/${this.userSessionId}/lists/${listId}`,{
        method:"PUT",
        headers: this.createHeaders(),
        body:JSON.stringify({
            "name": name,
            "countries":countries
        })
      })
    } catch(error){
        console.log(error)
    }
  }
    
  async deleteUserList(listId) {
    try{
      return await fetch(`${this.BASE_URL}/api/user/${this.userSessionId}/lists/${listId}`,{
            method:"DELETE",
            headers: this.createHeaders()
            });
    }catch(e){
      console.log(e)
    }
  }

  async getCountriesDataByDays(iso,startDay,endDay) {
    try{
      return await fetch(`${this.BASE_URL}/api/countries/timeseries?countries=${iso}&fromDay=${startDay}&toDay=${endDay}`, {
        method:'GET',
        headers: this.createHeaders(),
      });
    }catch(error){
      console.log(error)
    }
  }

  async getCountriesData(isoList) {
    try{
      return await fetch(`${this.BASE_URL}/api/countries/timeseries?countries=${isoList}`, {
        method:'GET',
        headers: this.createHeaders(),
      });
    }catch(error){
      console.log(error)
      }
  }
  
  async getNearCountries() {
    try{

      //let position = await getLocation()
      //const lat = position.coords.latitude;
      //const lng = position.coords.longitude;
      //return await fetch(`${this.BASE_URL}/api/countries?lat=${lat}&lon=${lng}`, {   
      return await fetch(`${this.BASE_URL}/api/countries?lat=-34&lon=-58`, {
        method: 'GET',
        headers: this.createHeaders()
      });

    }catch(e){
      console.log(e)
    }
  }

  async loginUser(loginValue,passwordValue) {
    try{
      return await fetch(`${this.BASE_URL}/api/login`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(
            {
              email: loginValue,
              password: passwordValue
            }),
        });
    }catch(error){
      console.log(error)
    } 
  }

  async loginUserWithGoogle(tokenId) {
    try {
      return await fetch(`${this.BASE_URL}/api/auth/google?token=${tokenId}`, {
        method: 'POST',
        headers: this.createHeaders()
      });
    } catch (error) {
      console.log(error)
    }
  }

  async createUser(nameValue,loginValue,passwordValue,countryIso) {
    try{
      return await fetch(`${this.BASE_URL}/api/signup`, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify({
                        name: nameValue,
                        email: loginValue,
                        password: passwordValue,
                        country: countryIso,
                        isAdmin: false
                        }),
                      });
    } catch(error){
      console.log(error)
    }
  }

  async getAllReports(){
    return await fetch(`${this.BASE_URL}/api/admin/report/`,{
      method:'GET',
      headers: this.createHeaders(),
    })
  }

  async getUserReport(unIdUsuario){
    return await fetch(`${this.BASE_URL}/api/admin/report/${unIdUsuario}`,{
      method:'GET',
      headers: this.createHeaders(),
  })
  }
  async getInterestedInCountry(paisIsoCode){
    return await fetch(`${this.BASE_URL}/api/admin/report/${paisIsoCode}/list`,{
      method:'GET',
      headers: this.createHeaders(),
  })
  }

  async compareLists(idLista1,idLista2){
    return await fetch(`${this.BASE_URL}/api/admin/report/lists/compare?list1=${idLista1}&list2=${idLista2}`,{
      method:'GET',
      headers: this.createHeaders(),
  })
  }


  async getAmountOfListsFromTo(fechaInicio, fechaFin){
    console.log(this.BASE_URL + "/api/admin/report/lists?startDate=" + fechaInicio + "&endDate=" + fechaFin)
    return await fetch( this.BASE_URL + "/api/admin/report/lists?startDate=" + fechaInicio + "&endDate=" + fechaFin,{
      method:'GET',
      headers: this.createHeaders(),
  })
  }

}

export default Api;
  
