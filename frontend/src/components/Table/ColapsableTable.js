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
  IconButton,
  Collapse,
  Box
} from "@material-ui/core";
import { withStyles, makeStyles } from '@material-ui/core/styles';
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp';

// components
import useStyles from "../../views/user/dashboard/styles";
import { Typography } from "../Wrappers";

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

const useRowStyles = makeStyles({
  root: {
    '& > *': {
      borderBottom: 'unset',
    },
  },
});

export default function TableComponent({data}) {
  const classes = useStyles();
  const [page, setPage] = useState(0);
  const rowsPerPage = 2

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  return (
    <React.Fragment>
      <TableContainer className={Paper}>
      <Table className={classes.table} size="small" aria-label="a dense table">
        <TableHead>
            <TableRow>
              <StyledTableCell></StyledTableCell>
              <StyledTableCell align="right">Country</StyledTableCell>
              <StyledTableCell align="right">Infected</StyledTableCell>
              <StyledTableCell align="right">Recovered</StyledTableCell>
              <StyledTableCell align="right">Deceased</StyledTableCell>
            </TableRow>
        </TableHead>
        <TableBody>
        {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
          .map((row) => (
            <Row key={row._id} row={row} />
          ))}

        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[rowsPerPage]}
        component="div"
        count={data.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onChangePage={handleChangePage}
      />
      </TableContainer>
    </React.Fragment>
  );
}

function Row(props) {
  const { row } = props;
  const [open, setOpen] = React.useState(false);
  const classes = useRowStyles();

  return (
    <React.Fragment>
      <TableRow className={classes.root}>
        <TableCell>
          <IconButton aria-label="expand row" size="small" onClick={() => setOpen(!open)}>
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell align="right" component="th" scope="row">
          {row.countryregion}
        </TableCell>
        <TableCell align="right">{row.confirmed}</TableCell>
        <TableCell align="right">{row.deaths}</TableCell>
        <TableCell align="right">{row.recovered}</TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box margin={1}>
              <Typography variant="h6" gutterBottom component="div">
                Offset
              </Typography>
              <Table size="small" aria-label="purchases">
                <TableHead>
                  <TableRow>
                  </TableRow>
                </TableHead>
                <TableBody>
                </TableBody>
              </Table>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  );
}