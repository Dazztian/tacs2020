import React, {useState, useEffect} from 'react'
import { Grid} from "@material-ui/core";

// components
import Widget from "../../../components/Widget";
//import Table from "../../../components/Table/Table";
import MUIDataTable from "mui-datatables";

// styles
import useStyles from "./styles";

export default function Dashboard(props) {
  var classes = useStyles();

  const state = { rowsSelected: [] };

  const [unArrayTodosLosUsuarios,setunArrayTodosLosUsuarios] = useState([])
  const [unArrayUsuarioParticular,setunArrayUsuarioParticular] = useState({})
  const [count,setCount] = useState(0)


  const id_user= "5ed6afdefec8e000a9d826ae"
  const BASE_URL = 'http://localhost:8080';
  const tokenAdmin = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZDZhZmRlZmVjOGUwMDBhOWQ4MjZhZSIsImV4cCI6MTU5MjU1NTU2M30.bkHQqWkZkX389_wXvM1M5z5WHGZwG0jVOzev4w-yKKI"

  const obtenerInfoDeUsuarios = async ()=>{
    try{
    let res = await fetch(BASE_URL +"/api/admin/report/",{
        method:"get",
        headers:{
          'Accept': 'application/json',
          'Authorization' : 'Bearer '+ tokenAdmin
          }
    })
    let elemento = await res.json()
    /* [{  id: 0, name: "Mark Otto",  email: "ottoto@wxample.com"},*/
    let resultArray = await Promise.all(elemento)

    setunArrayTodosLosUsuarios(resultArray)
    console.log(resultArray)
    
    }
    catch(err) {
        console.log(err)
        window.alert(err)
    }
}


const obtenerInfoUsuarioParticular = async (unIdUsuario)=>{
  try{
      let res = await fetch(BASE_URL +"/api/admin/report/"+ unIdUsuario,{
      method:"get",
      headers:{
        'Accept': 'application/json',
        'Authorization' : 'Bearer '+ tokenAdmin
        }
  })
      let elemento = await res.json()

      let promArray =  [{ name:elemento.user.name, listsQuantity:elemento.listsQuantity, countriesTotal:elemento.countriesTotal }]

      let resultArray = await Promise.all(promArray)
      
      setunArrayUsuarioParticular(resultArray)
      
      window.alert("Ha seleccionado el User: "
                 +"\n Nombre: "+resultArray[0].name
                 +"\n Cant de listas: " +resultArray[0].listsQuantity
                 +"\n Cant de total de paises: " +resultArray[0].countriesTotal ) 
  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}

useEffect(() => {
  obtenerInfoDeUsuarios()
  //obtenerInfoUsuarioParticular()
}, [count]);    


  return (
    <>
      <Grid container spacing={4}>
        <Grid item xs={12}>        

        </Grid>
        <Grid item xs={12}>
          <Widget title="Usuarios"   upperTitle   noBodyPadding    bodyClass={classes.tableWidget} >
            <MUIDataTable
            title="Lista de usuarios"
            data={unArrayTodosLosUsuarios}
            columns={["id", "name", "email"]}
            options={{
              filter: true,
              selectableRows: 'multiple',
              selectableRowsOnClick: true,
              filterType: 'dropdown',
              responsive: 'stacked',
              rowsPerPage: 10,
              rowsSelected: state.rowsSelected,
            onRowsSelect:  (rowsSelected, allRows) => {
            allRows.map( item => obtenerInfoUsuarioParticular( unArrayTodosLosUsuarios[item.dataIndex].id ) )
            }
          }}
          />
          </Widget>
        </Grid>
      </Grid>
    </>
  );
}
  

