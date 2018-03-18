import React, { Component } from 'react';
import { LinearProgress as VendorLinearProgress } from 'material-ui';
import PropTypes from 'prop-types';

export class LinearProgress extends Component {

  render() {
    const {
      isFetching,
	} = this.props;

    if(!isFetching) {
      return null;
	}

	return (
	  <VendorLinearProgress mode={'indeterminate'} color={'red'}/>
	)
  }

}


LinearProgress.propTypes = {
  isFetching: PropTypes.bool,
}

LinearProgress.defaultProps = {
  isFetching: false,
}


