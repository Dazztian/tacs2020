import React, { useEffect } from 'react';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import Grid from '@material-ui/core/Grid';
import ListTotalStats from "../ListStat/ListTotalStat";
import PageTitle from "../PageTitle";


const headCells = [
  { id: 'Country', numeric: true, disablePadding: false, label: 'Country' },
  { id: 'Infected', numeric: true, disablePadding: false, label: 'Infected' },
  { id: 'Recovered', numeric: true, disablePadding: false, label: 'Recovered' },
  { id: 'Deceased', numeric: true, disablePadding: false, label: 'Deceased' },
];

const StyledTableCell = withStyles((theme) => ({
    head: {
      backgroundColor:theme.palette.primary.main,
      color: theme.palette.common.white,
    },
    body: {
      fontSize: 14,
    }}))(TableCell);

function EnhancedTableHead(props) {

  return (
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
  );
}

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

export default function EnhancedTable({initialCountryIso,totalCountries=[]}) {
  const classes = useStyles();
  const [selected, setSelected] = React.useState();
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);
  const [actualCountryData,setMainCountryData] = React.useState()
  

  const handleClick = async (event, iso2) => {
    if(iso2!==selected){
        setMainCountryData(await totalCountries.filter(country => country.iso2===iso2)[0])
        setSelected(iso2);
    }
  };

  useEffect((event)=>{
    handleClick(event,initialCountryIso)
  },[initialCountryIso])

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const isSelected = (name) => !!selected ? selected.indexOf(name) !== -1 : false

  const emptyRows = rowsPerPage - Math.min(rowsPerPage, totalCountries.length - page * rowsPerPage)

  return (
    <div className={classes.root}>
      <Grid container>
        <Grid item lg={12} md={12} sm={12} xs={12}> 
          {!!actualCountryData && <PageTitle title= {`${actualCountryData.countryRegion} today`} />}  
        </Grid>
      <Grid container spacing={5}>  
      <Grid item lg={6} md={6} sm={12} xs={12}>
        {!!actualCountryData && <ListTotalStats actualCountryData={actualCountryData} />}
      </Grid>
      <Grid item lg={6} md={6} sm={12} xs={12}>
        <TableContainer>
          <Table
            className={classes.table}
            aria-labelledby="tableTitle"
            size='small'
            aria-label="enhanced table"
          >
            <EnhancedTableHead
              classes={classes}
              rowCount={totalCountries.length}
            />
            <TableBody>
              {totalCountries.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row, index) => {
                  const isItemSelected = isSelected(row.iso2);
                  return (
                    <TableRow
                      hover
                      onClick={(event) => handleClick(event, row.iso2)}
                      role="checkbox"
                      aria-checked={isItemSelected}
                      tabIndex={-1}
                      key={row.iso2}
                      selected={isItemSelected}
                    >
                      <TableCell align="center">{row.countryRegion}</TableCell>
                      <TableCell align="center">{row.confirmed}</TableCell>
                      <TableCell align="center">{row.recovered}</TableCell>
                      <TableCell align="center">{row.deaths}</TableCell>
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
          count={totalCountries.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onChangePage={handleChangePage}
          onChangeRowsPerPage={handleChangeRowsPerPage}
        />
        </TableContainer>
        </Grid>
      </Grid>
    </Grid> 
    </div>
  );
}
