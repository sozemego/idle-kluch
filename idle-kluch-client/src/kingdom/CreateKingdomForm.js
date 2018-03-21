import React, { Component } from "react";
import { connect } from "react-redux";


class CreateKingdomForm extends Component {

  constructor(props) {
    super(props);
  }

  render() {
    return (
        <div>
            CREATE NEW KINGDOM
        </div>
    );
  }

}

const mapStateToProps = (state) => {
  return {

  };
};

export default connect(mapStateToProps, {})(CreateKingdomForm);