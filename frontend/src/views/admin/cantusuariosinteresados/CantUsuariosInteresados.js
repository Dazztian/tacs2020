import React, {useState, useEffect} from 'react'

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";

import Api from "../../../apis/Api"

const CantUsuariosInteresados = ()=>{
    const api = new Api()

    const state = { rowsSelected: [] };

    const [unArray,setUnArray] = useState([])
    const [CantUsuariosInteresados, setCantUsuariosInteresados]=useState({})
    const [loading,setLoading] = useState(false)

    const [count,setCount] = useState(0)

    const obtenerListaDePaises = async ()=>{
        try{
        const res= await api.getCountryList()
        let elemento = await res.json()

        let promArray = elemento.map( item => {  return [ item.name, item.iso2] })
        
        let resultArray = await Promise.all(promArray)

        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    const obtenerCantUsuariosInteresados= async (nombreDelPais, paisIsoCode) =>{
        try{
        const res = await api.getInterestedInCountry(paisIsoCode)
        let elemento = await res.json()

        let promArray = elemento.totalUsers

        window.alert("Cant de usuarios interesados en "+nombreDelPais+ ": " + promArray)
        console.log(promArray)
        
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        setLoading(true)
        obtenerListaDePaises()
        setLoading(false)
    }, [count]);    



return(
    <>
        <PageTitle title="Escoja al menos un pais para obtener la cant de usuarios interesados" />
        <MUIDataTable
            title="Lista de paises"
            data={unArray}
            columns={["Nombre", "iso2"]}
            options={{
                filter: true,
                selectableRows: 'single',
                selectableRowsOnClick: true,
                filterType: 'dropdown',
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsSelected: state.rowsSelected,
            onRowsSelect:  (rowsSelected, allRows) => {
            allRows.map( item => obtenerCantUsuariosInteresados(unArray[item.dataIndex][0],unArray[item.dataIndex][1]) );
            },
            }
            }
        />
    </>
    )

}    

export default CantUsuariosInteresados
