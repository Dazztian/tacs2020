import React, {useState, useEffect} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Container } from '@material-ui/core';

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";

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

        let promArray = elemento.map( item => {        
            return [ item.countryregion, item._id]
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

return(
    <>
        {loading} ? <div>loading...poner una ruedita girando</div> :
        <MUIDataTable
            title="FILTRADO"
            data={unArray}
            columns={["countryregion", "id"]}
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
