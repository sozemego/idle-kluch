import React, { Component } from "react";
import { LinearProgress as VendorLinearProgress } from "@material-ui/core";
import PropTypes from "prop-types";

export class LinearProgress extends Component {
  render() {
    const { isFetching } = this.props;

    return (
      <VendorLinearProgress
        mode={"indeterminate"}
        color={"primary"}
        style={isFetching ? {} : { visibility: "hidden" }}
      />
    );
  }
}

LinearProgress.propTypes = {
  isFetching: PropTypes.bool,
};

LinearProgress.defaultProps = {
  isFetching: false,
};
