import mock from "./mock"

class Api {

    constructor() {
      this.authToken = localStorage.getItem("id_token");
      this.userSessionId = localStorage.getItem("id_session");
      this.countryMap = localStorage.getItem("countriesList");
    }
  
    headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    };
  
    BASE_URL = 'https://ab7c3ed5ca13.ngrok.io'; 

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
      const data = mock.nearordered 

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })
    }catch(error){
      console.log(error)
    
    /*const res = await await fetch(this.BASE_URL, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });

      if(res.ok)
        const data = await res.json()
      
      return data.timeseries
      }*/
  }
}
  
  async getNearCountries() {
    try{
      const data = mock.nearordered

      return new Promise(resolve => {
        setTimeout(() => {
          resolve(data);
        }, 500);
      })

      /*let position = await getLocation()
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;

      return await fetch(`${this.BASE_URL}/api/countries?lat=${lat}&lon=${lng}}`, {
        method: 'GET',
        headers: this.createHeaders()
      });*/

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
    const res = mock.loginUser
    
    try{
      return await fetch(`${this.BASE_URL}/auth/google`, {
        method:'POST',
        headers: this.createHeaders(),
        body: JSON.stringify(
          {
            token: tokenId
          }),
      });
    }catch(error){
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

  async compareLists(idLista1,idLista2){
    return await fetch(`${this.BASE_URL}/api/admin/report/lists/compare?list1=${idLista1}&list2=${idLista2}`,{
      method:'GET',
      headers: this.createHeaders(),
  })
  }

}

export default Api;
  