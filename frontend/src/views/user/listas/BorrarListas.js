import React, {useState, useEffect} from 'react'
import { Grid, Container } from '@material-ui/core';

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";
import Api from '../../../apis/Api';
const api = new Api()

const BorrarListas = ()=>{


    const [unArray,setUnArray] = useState([])
    const [selectedLists,setSelectedLists] = useState([])
    const [nameArray,setNameArray] = useState([])
    const [loading,setLoading] = useState(false)

    const obtenerListaDePaisesXUsuario = async ()=>{
        try{
            let res = await api.getUserLists()
            //let elemento = await res.json();

            let promArray = res.map( list =>{ 
                return [list.name, list.id]
            })

            let resultArray = await Promise.all(promArray)
            console.log(resultArray)
            setUnArray(resultArray)  
           

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

    //{{localPath}}/api/user/{{lastUserId}}/lists/5ed9051d66236a4280ec9603
    const borrarListasSeleccionada= async ()=>{
        try{
            let promArray = selectedLists.map( item => unArray[item.dataIndex][1]).map(listId => api.deleteUserList(listId))
            Promise.all(promArray);
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

    useEffect(() => {
        obtenerListaDePaisesXUsuario()
    }, []); 

    return(
            <>
            <PageTitle title="Delete a list" />            

            {
            <Grid container spacing={4}>
            <Grid item lg={12} md={12} sm={12} xs={12}>
            <MUIDataTable
            title={""}
            data={unArray}
            columns={["List name","Id"]}
            options={{
                selectableRows: 'multiple',
                selectableRowsOnClick: true,
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsPerPageOptions: [10],
                print: false,
                viewColumns: false,
                fixedHeader: false,
                download: false,
                filter: false,
                onRowsSelect:  (rowsSelected, allRows) => {
                    setSelectedLists(allRows)
                },
                onRowsDelete: ()=>{ borrarListasSeleccionada()}
            }
            }
            />
            </Grid>
            </Grid>
            
            }
            </>
        )
}   


export default BorrarListas 