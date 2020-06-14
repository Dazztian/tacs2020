import React, {useState, useEffect} from "react";
import {
  Table,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  TableContainer,
  Grid,
  Paper,
  TablePagination,
  Button,
  TextField,
  PropTypes 
} from "@material-ui/core";
import { withStyles } from '@material-ui/core/styles';

// components
import useStyles from "../../views/user/dashboard/styles";
import ListTotalStats from "../ListStat/ListTotalStat";
import PageTitle from "../PageTitle";

const StyledTableCell = withStyles((theme) => ({
  head: {
    backgroundColor:theme.palette.primary.main,
    color: theme.palette.common.white,
  },
  body: {
    fontSize: 14,
  }

}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);

const rowAlign = "center"

export default function TableComponent({setIsLoading,initialCountryIso,totalCountries}) {
  console.log(initialCountryIso)
  console.log(totalCountries)
  //let actualCountryIso = initialCountryIso
  //const [actualCountryIso,setMainCountryIso] = useState(initialCountryIso)
  const [actualCountryData,setMainCountryData] = useState()
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const classes = useStyles();
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  async function updateMainCountry(actualCountryIso){
    console.log(totalCountries)
    const data = await totalCountries.filter(country => country.countrycode===actualCountryIso)[0]
    console.log(data)
    setMainCountryData(await totalCountries.filter(country => country.countrycode===actualCountryIso)[0])
  }

  useEffect(() => {
      updateMainCountry(initialCountryIso);
  },[initialCountryIso])

  const emptyRows = rowsPerPage - Math.min(rowsPerPage, totalCountries.length - page * rowsPerPage);

  return (

    <Grid container>
        <Grid item lg={12} md={12} sm={12} xs={12}> 
          {!!actualCountryData && <PageTitle title= {`${actualCountryData.countryregion} today`} />}  
        </Grid>
      <Grid container spacing={5}>  
      <Grid item lg={6} md={6} sm={12} xs={12}>
        {!!actualCountryData && <ListTotalStats actualCountryData={actualCountryData} />}
      </Grid>
      <Grid item lg={6} md={6} sm={12} xs={12}>
        <TableContainer>
        <Table className={classes.table} size="small" aria-label="a dense table">
          <TableHead>
              <TableRow>
                <StyledTableCell align={rowAlign}>Country</StyledTableCell>
                <StyledTableCell align={rowAlign}>Infected</StyledTableCell>
                <StyledTableCell align={rowAlign}>Recovered</StyledTableCell>
                <StyledTableCell align={rowAlign}>Deceased</StyledTableCell>
              </TableRow>
          </TableHead>
          <TableBody>
              {totalCountries.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(({ countrycode, countryregion, confirmed, deaths, recovered}) => (
                  <StyledTableRow onClick={() => updateMainCountry(countrycode)} align={rowAlign} key={countrycode}>
                    <StyledTableCell align={rowAlign}>{countryregion}</StyledTableCell>
                    <StyledTableCell align={rowAlign}>{confirmed}</StyledTableCell>
                    <StyledTableCell align={rowAlign}>{recovered}</StyledTableCell>
                    <StyledTableCell align={rowAlign}>{deaths}</StyledTableCell>
                  </StyledTableRow>
                ))}
                {emptyRows > 0 && (
                  <TableRow style={{ height: (30) * emptyRows }}>
                    <TableCell colSpan={6} />
                  </TableRow>
                )}
            </TableBody>
        </Table>
        <TablePagination
          rowsPerPageOptions={[rowsPerPage]}
          component="div"
          count={totalCountries.length}
          rowsPerPage={rowsPerPage}
          page={page}
          SelectProps={{
            inputProps: { 'aria-label': 'rows per page' },
            native: true,
          }}
          onChangePage={handleChangePage}
        />
        </TableContainer>
      </Grid>
      </Grid>
    </Grid>    
    );
}
