import React, {useState, useEffect} from 'react'
import { Grid, CircularProgress, Paper } from '@material-ui/core';

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";
import Api from '../../../apis/Api';


const BorrarListas = ()=>{
    const api = new Api()

    const [unArray,setUnArray] = useState([])
    const [selectedLists,setSelectedLists] = useState([])
    const [loading,setLoading] = useState(true)

    const obtenerListaDePaisesXUsuario = async ()=>{
        try{
            let res = await api.getUserLists()
            let data = await res.json();

            let promArray = data.map( list =>{ 
                return [list.name, list.id]
            })

            let resultArray = await Promise.all(promArray)
            setUnArray(resultArray)  
            setLoading(false)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

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
            {
            loading 
				? 
				<Paper>
					<Grid
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
				</Paper>
				: 
				<div>
            <PageTitle title="Delete a list" />            
            
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
            </div>
            }
            </>
        )
}   


export default BorrarListas 