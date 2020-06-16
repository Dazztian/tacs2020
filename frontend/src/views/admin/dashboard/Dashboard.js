import React, { useState } from "react";
import { Grid} from "@material-ui/core";
import { useTheme } from "@material-ui/styles";

// components
import mock from "./mock";
import Widget from "../../../components/Widget";
import { Typography } from "../../../components/Wrappers";
import Dot from "../../../components/Sidebar/components/Dot";
import BigStat from "../../../components/BigStat/BigStat";

const mainChartData = getMainChartData();

export default function Dashboard(props) {
  var classes = useStyles();
  var theme = useTheme();

  // local
  var [mainChartState, setMainChartState] = useState("monthly");

  return (
    <>
      <Grid container spacing={4}>
        <Grid item xs={12}>        
        </Grid>
        {mock.bigStat.map(stat => (
          <Grid item md={4} sm={6} xs={12} key={stat.product}>
            <BigStat {...stat} />
          </Grid>
        ))}
        <Grid item xs={12}>
          <Widget
            title="Usuarios"
            upperTitle
            noBodyPadding
            bodyClass={classes.tableWidget}
          >
            <Table data={mock.table} />
          </Widget>
        </Grid>
      </Grid>
    </>
  );
}

return resultArray;
  

