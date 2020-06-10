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
  const rowsPerPage = 5

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  return (
      <TableContainer className={Paper}>
      <Table className={classes.table} size="small" aria-label="a dense table">
        <TableHead>
            <TableRow>
              <StyledTableCell align={rowAlign}>Country</StyledTableCell>
              <StyledTableCell align={rowAlign}>Infected</StyledTableCell>
              <StyledTableCell align={rowAlign}>Recovered</StyledTableCell>
              <StyledTableCell align={rowAlign}>Deceased</StyledTableCell>
              <StyledTableCell align={rowAlign}>Since</StyledTableCell>
              <StyledTableCell align={rowAlign}>Until</StyledTableCell>
              <StyledTableCell align={rowAlign}>Offset</StyledTableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(({ _id, countryregion, confirmed, deaths, recovered}) => (
                <StyledTableRow align={rowAlign} key={_id}>
                  <StyledTableCell align={rowAlign}>{countryregion}</StyledTableCell>
                  <StyledTableCell align={rowAlign}>{confirmed}</StyledTableCell>
                  <StyledTableCell align={rowAlign}>{recovered}</StyledTableCell>
                  <StyledTableCell align={rowAlign}>{deaths}</StyledTableCell>
                  <StyledTableCell align={rowAlign}>
                    <form className={classes.container} noValidate>
                      <TextField
                        id="date1"
                        type="date"
                        defaultValue={Date.now()}
                        InputLabelProps={{
                          shrink: true,
                        }}
                      />
                    </form>
                  </StyledTableCell>
                  <StyledTableCell align={rowAlign}>
                    <form className={classes.container} noValidate>
                    <TextField
                      id="date2"
                      type="date"
                      defaultValue={Date.now()}
                      InputLabelProps={{
                        shrink: true,
                      }}
                    />
                    </form>
                  </StyledTableCell>
                  <StyledTableCell align={rowAlign}>
                    <Button                 
                      className="px-2"
                      variant="contained" 
                      color="primary"
                    > Set
                    </Button>
                  </StyledTableCell>
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
