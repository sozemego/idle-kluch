import React, { Component } from "react";
import { connect } from "react-redux";

import styles from "./create-kingdom-form.css";
import { Divider, Button, TextField } from "@material-ui/core";

import * as kingdomActions from "./actions";
import { getKingdomNameRegistrationError } from "./selectors";

class CreateKingdomForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdomName: "",
    };
  }

  onSubmit = () => {
    const { kingdomName } = this.state;

    return this.props.registerKingdom(kingdomName);
  };

  onKingdomNameChange = (e, kingdomName) => {
    this.setState({ kingdomName: e.target.value });
  };

  render() {
    const { kingdomNameRegistrationError } = this.props;
    const { kingdomName } = this.state;
    const { onKingdomNameChange, onSubmit } = this;

    return (
      <div className={styles.container}>
        <h2>Create new kingdom</h2>
        <Divider/>
        <div className={styles.input_row}>
          <TextField
            value={kingdomName}
            onChange={onKingdomNameChange}
            label={"Kingdom name"}
            error={Boolean(kingdomNameRegistrationError)}
          />
        </div>
        {kingdomNameRegistrationError}
        <Button
          onClick={onSubmit}
          className={styles.submit_button}
          variant={"outlined"}
        >
          Create kingdom!
        </Button>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    kingdomNameRegistrationError: getKingdomNameRegistrationError(state),
  };
};

export default connect(mapStateToProps, kingdomActions)(CreateKingdomForm);
