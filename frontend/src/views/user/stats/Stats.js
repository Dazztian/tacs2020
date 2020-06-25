import React, { useState, useEffect } from "react";
import { getCountry } from "../../../apis/GeolocationApi"
import { lighten, makeStyles, withStyles } from '@material-ui/core/styles';
import {
  Grid,
  CircularProgress,
  TableContainer,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TablePagination,
  TableRow,
  Button
} from "@material-ui/core";

// api
import Api from '../../../apis/Api';

// components
import PageTitle from "../../../components/PageTitle";
import { useUserState } from "../../../context/UserContext";
import ListStats from "../../../components/ListStat/ListStat";
import TotalStats from "../../../components/Table/TableEnhanced";

const api = new Api();

export default function Stats(props) {

  const useStyles = makeStyles((theme) => ({
    root: {
      width: '100%',
    },
    paper: {
      width: '100%',
      marginBottom: theme.spacing(2),
    },
    table: {
      minWidth: 300,
    },
    visuallyHidden: {
      border: 0,
      clip: 'rect(0 0 0 0)',
      height: 1,
      margin: -1,
      overflow: 'hidden',
      padding: 0,
      position: 'absolute',
      top: 20,
      width: 1,
    },
  }));

  // local
  const [isLoading, setIsLoading] = useState(true);
  const [userLists, setUserLists] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [selectedCountryIsoList,setSelectedCountryList] = useState()
  const [countryInitialData,setCountriesInitialData] = useState()
  
  const classes = useStyles();

  async function fetchUserListData() {
    try {
      const res = await api.getUserLists()
      const userLists = await res.json()
      setUserLists(userLists)
    } catch(error) {
      console.log(error)
    }
  }

  useEffect(() => { //tiene que haber un useEffect por cada variable de estado de chart a modificar
    async function getInitialData(){
      await fetchUserListData()
      setIsLoading(false)
    }
    getInitialData();
  },[]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleClick = async (event, row) => {
      setIsLoading(true)
      let isoList = await row.countries.map(country => country.iso2)
        const countriesData = await api.getCountriesData(isoList)
        setCountriesInitialData(countriesData)
        setSelectedCountryList(isoList)
        setIsLoading(false)
  };

  const headCells = [
    { id: 'List name', numeric: false, disablePadding: false, label: 'List name' },
    { id: 'Countries', numeric: true, disablePadding: false, label: 'Countries' },
  ];

  const StyledTableCell = withStyles((theme) => ({
    head: {
      backgroundColor:theme.palette.primary.main,
      color: theme.palette.common.white,
    },
    body: {
      fontSize: 14,
    }}))(TableCell);

    const emptyRows = rowsPerPage - Math.min(rowsPerPage, userLists.length - page * rowsPerPage);

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
    : <div className={classes.root}>
      { !selectedCountryIsoList ?
      <Grid container>
        <PageTitle title= {`Select a list`} />  
        <Grid item lg={12} md={12} sm={12} xs={12}>
          <TableContainer>
            <Table
              className={classes.table}
              aria-labelledby="tableTitle"
              size='small'
              aria-label="enhanced table"
            >
              <TableHead>
                <TableRow>
                  {headCells.map((headCell) => (
                    <StyledTableCell
                      key={headCell.id}
                      align={headCell.numeric ? 'center' : 'left'}
                      padding={headCell.disablePadding ? 'none' : 'default'}
                    >
                      {headCell.label}
                    </StyledTableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {userLists.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => {
                    return (
                      <TableRow
                        hover
                        onClick={(event) => handleClick(event, row)}
                        role="checkbox"
                        tabIndex={-1}
                        key={row.id}
                      >
                        <TableCell align="left">{row.name}</TableCell>
                        <TableCell align="center">{row.countries.map(c=>c.name).join(', ')}</TableCell>
                      </TableRow>
                    );
                  })}
                {emptyRows > 0 && (
                  <TableRow style={{ height: (33) * emptyRows }}>
                    <TableCell colSpan={6} />
                  </TableRow>
                )}
              </TableBody>
            </Table>
          <TablePagination
            rowsPerPageOptions={[5]}
            component="div"
            count={userLists.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onChangePage={handleChangePage}
          />
          </TableContainer>
          </Grid>
      </Grid> 
      :
        <Grid container>
          <Grid item xs={12} align="right">
            <Button
              onClick={() => {setSelectedCountryList(null); setSelectedCountryList(null)}}
              classes={{ root: classes.button }}
              variant="contained"
              size="large"
              color="secondary"
            > 
              Back
            </Button>
          </Grid>
          <Grid item xs={12}>
          <TotalStats initialCountryIso={selectedCountryIsoList[0]} totalCountries={countryInitialData}/>
          </Grid>
        <Grid container>
          <Grid item xs={12}>
            <PageTitle title= {"Timeline search"} />
            <ListStats isoList={selectedCountryIsoList}/>
          </Grid>
        </Grid>
      </Grid>
      }
  </div>
      }
    </>
  );
}
