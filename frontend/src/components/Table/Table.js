import React, {useState} from "react";
import {
  Table,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  TableContainer,
  Paper,
  TablePagination,
  Button,
  TextField,
  PropTypes 
} from "@material-ui/core";
import { withStyles } from '@material-ui/core/styles';

// components
import useStyles from "../../views/user/dashboard/styles";

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

export default function TableComponent({data}) {
  const classes = useStyles();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const emptyRows = rowsPerPage - Math.min(rowsPerPage, data.length - page * rowsPerPage);

  return (
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
            {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(({ countryregion, confirmed, deaths, recovered}) => (
                <StyledTableRow align={rowAlign} key={countryregion}>
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
        count={data.length}
        rowsPerPage={rowsPerPage}
        page={page}
        SelectProps={{
          inputProps: { 'aria-label': 'rows per page' },
          native: true,
        }}
        onChangePage={handleChangePage}
      />
      </TableContainer>
  );
}
