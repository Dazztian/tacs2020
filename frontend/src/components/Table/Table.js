import React, {useState} from "react";
import {
  Table,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  TableContainer,
  Paper,
  TablePagination
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
  },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);


export default function TableComponent({data}) {
  const classes = useStyles();
  const [page, setPage] = useState(0);
  const rowsPerPage = 5

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  return (
      <TableContainer className={Paper}>
      <Table className={classes.table} size="small" aria-label="a dense table">
        <TableHead>
            <TableRow>
              <StyledTableCell align="right">Country</StyledTableCell>
              <StyledTableCell align="right">Infected</StyledTableCell>
              <StyledTableCell align="right">Recovered</StyledTableCell>
              <StyledTableCell align="right">Deceased</StyledTableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(({ _id, countryregion, confirmed, deaths, recovered}) => (
                <StyledTableRow key={_id}>
                  <StyledTableCell align="right">{countryregion}</StyledTableCell>
                  <StyledTableCell align="right">{confirmed}</StyledTableCell>
                  <StyledTableCell align="right">{recovered}</StyledTableCell>
                  <StyledTableCell align="right">{deaths}</StyledTableCell>
                </StyledTableRow>
              ))}
          </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[5]}
        component="div"
        count={data.length}
        rowsPerPage={5}
        page={page}
        onChangePage={handleChangePage}
      />
      </TableContainer>
  );
}