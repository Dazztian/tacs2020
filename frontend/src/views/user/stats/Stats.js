import React, { useState, useEffect } from "react";
import { getCountry } from "../../../apis/GeolocationApi"
import {
  Grid,
  CircularProgress,
} from "@material-ui/core";

// api
import Api from '../../../apis/Api';

// components
import PageTitle from "../../../components/PageTitle/PageTitle";
import ListStats from "../../../components/ListStat/ListStat";

import TableComponent from "../../../components/Table/Table";
import TotalStats from "../../../components/Table/TableEnhanced";

const api = new Api();

export default function UserListStats(props) {
  // local
  var [userList, setUserList] = useState();
  var [isoList, setIsoList] = useState();
  var [isLoading, setIsLoading] = useState(true);

  async function fetchUserList() {
    try {

    } catch(error) {
      console.log(error)
    }
  }


  useEffect(() => { //tiene que haber un useEffect por cada variable de estado de chart a modificar
    async function getInitialData(){
      await fetchUserList()
      setIsLoading(false)
    }
    getInitialData()
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
    : <Grid container
        direction="column"
        alignItems="center"
        justify="center"    
      >

      </Grid>
    }
    {!!userList && 
      <div>
        <Grid container>
          <Grid item xs={12}>
          <TotalStats initialCountryIso={userList[0].iso2} totalCountries={userList}/>
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
