import React, { useState, useEffect } from "react";
import { getCountry } from "../../../apis/GeolocationApi"
import {
  Grid,
  CircularProgress,
} from "@material-ui/core";

// api
import Api from '../../../apis/Api';

// components
import PageTitle from "../../../components/PageTitle";
import ListStats from "../../../components/ListStat/ListStat";
import TotalStats from "../../../components/Table/TableEnhanced";

const api = new Api();

export default function Dashboard(props) {
  // local
  var [localCountry, setLocalIso] = useState("");
  var [isLoading, setIsLoading] = useState(true);
  var [nearCountriesOrder, setNearCountriesOrder] = useState();
  var [isoList, setIsoList] = useState();

  
  async function fetchNearData() {
    try {
      const nearOrder = await api.getNearCountries()
      setNearCountriesOrder(nearOrder)
      setIsoList(await Promise.all(nearOrder.map(n => n.iso2)))
    } catch(error) {
      console.log(error)
    }
  }

  async function fetchLocal() {
    try {
      let iso = localStorage.getItem('tracker_country_Iso')
      console.log(iso)
      if (!iso){
        const {countryIso,countryName} = await getCountry()
        iso = countryIso

        localStorage.setItem('tracker_country', countryName)
        localStorage.setItem('tracker_country_Iso', countryIso)
      }
      setIsLoading(false)
      setLocalIso(iso)
    } catch(error) {
      console.log(error)
    }
  }

  useEffect(() => { //tiene que haber un useEffect por cada variable de estado de chart a modificar
    async function getInitialData(){
      await fetchNearData()
      await fetchLocal()

    }
    getInitialData();
  },[]);

  return (
    <>

    {isLoading 
    ? <Grid
        container
        spacing={0}
        direction="column"
        alignItems="center"
        justify="center"
        style={{ minHeight: '80vh' }}
      >
        <Grid item xs={3}>
          <CircularProgress size={100}/>
        </Grid>   
      </Grid> 
    : <div>
        <Grid container>
          <Grid item xs={12}>
          <TotalStats initialCountryIso={localCountry} totalCountries={nearCountriesOrder}/>
          </Grid>
        </Grid>
        <Grid container>
          <Grid item xs={12}>
            <PageTitle title= {"Timeline search"} />
            <ListStats isoList={isoList}/>
          </Grid>
        </Grid>
      </div>
      }
    </>
  );
}
