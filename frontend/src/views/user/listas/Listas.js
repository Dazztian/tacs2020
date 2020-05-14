import React, {useState, useEffect} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Container } from '@material-ui/core';

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";

   

/*
.then( elemento =>{ //elemento es el json con todos los registros
            elemento.map( (item)=>{        
                nuevoArray.push([
                     item.countryregion,
                     item._id]
                 );
            })
            //window.alert(JSON.stringify(nuevoArray.slice(1, 5)))
        })
*/



/*
const Listas = ()=>{

    var nuevoArray = []

    const [unArray,setUnArray] = useState([])
    
    const submitData = ()=>{
        fetch("http://localhost:8080/api/countries",{
            method:"GET",
            headers:{
              'Content-Type': 'application/json'
            }
        })
        .then(res=>res.json()) 
        .then( elemento =>{ //elemento es el json con todos los registros
            elemento.map( (item)=>{        
                nuevoArray.push([
                     item.countryregion,
                     item._id]
                 );
            })
        })
        .then( ()=> setUnArray(nuevoArray))
        .catch(err=>{
            window.alert(err)
      })
    }
*/

const Listas = ()=>{
    
    const [unArray,setUnArray] = useState([])
    const [loading,setLoading] = useState(false)
    const [count,setCount] = useState(0)
    //esta funcion podria ir definida en el archivo api.js
    const submitData = async ()=>{
        try{
        let res = await fetch("http://localhost:8080/api/countries",{
            method:"GET",
            headers:{
                'Content-Type': 'application/json'
            }
        })
        let elemento = await res.json()

        let promArray = []
        elemento.forEach( item => {        
            promArray.push([
                item.countryregion,
                item._id]
            );
        })
        let resultArray = await Promise.all(promArray)

        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        setLoading(true)
        submitData()
        setLoading(false)
    }, [count]);


const datatableData = [
    ["Joe James", "Example Inc.", "Yonkers", "NY"],
    ["John Walsh", "Example Inc.", "Hartford", "CT"]
    ]
const data2 = [
    ["Albania","5ebab361a5e1ec7c169b9ffb"],
    ["Algeria","5ebab361a5e1ec7c169b9ffc"],
    ["Andorra","5ebab361a5e1ec7c169b9ffd"],
    ["Angola","5ebab361a5e1ec7c169b9ffe"]
]    

return(
        <>
        <Button style={styles.botones} 
                mode="contained" 
                theme={theme}
                onPress={() => setCount(count+1)}>
            Obtener Lista de países
        </Button>       
        <MUIDataTable
            title="Employee List"
            data={datatableData}
            columns={["Name", "Company", "City", "State"]}
            options={{ filterType: "checkbox", }}
        />
        {loading} ? <div>loading...poner una ruedita girando</div> :
        <MUIDataTable
            title="FILTRADO"
            data={unArray}
            columns={["id", "countryregion"]}
            options={{ filterType: "checkbox", }}
        />
    </>
    )

}    

const theme = {
    colors:{
        primary:"#006aff"

    }
}

const styles = StyleSheet.create({
    root:{
        flex:1
    },
    botones:{
        marginTop:5
    },
    inputStyle:{
        margin:5
    }

})

export default Listas
