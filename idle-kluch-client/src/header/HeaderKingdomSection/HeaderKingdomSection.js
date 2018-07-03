import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import * as appActions from "../../app/actions";
import * as kingdomActions from "../../kingdom/actions";

import avatar from "../avatar_temp.png";
import styles from "./header-kingdom-section.css";
import { getKingdom, hasKingdom, isDeletingKingdom } from "../../kingdom/selectors";
import Menu from "@material-ui/core/es/Menu/Menu";
import MenuItem from "@material-ui/core/es/MenuItem/MenuItem";

class HeaderKingdomSection extends Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdomMenuAnchor: null,
    };
  }

  onContainerClicked = event => {
    event.preventDefault();
    this.setState({
      kingdomMenuAnchor: event.currentTarget,
    });
  };

  render() {
    const { hasKingdom, kingdom, deleteKingdom, deletingKingdom } = this.props;

    if (!hasKingdom) {
      return null;
    }

    const { kingdomMenuAnchor } = this.state;

    return (
      <Fragment>
        <div
          className={styles.name__container}
          onClick={this.onContainerClicked}
          aria-owns={kingdomMenuAnchor ? "simple-menu" : null}
          aria-haspopup="true"
        >
          <img src={avatar} className={styles.avatar} alt={"Kingdom avatar"}/>
          <div className={styles.name}>{kingdom.name}</div>
          <i className={"material-icons"}>arrow_drop_down</i>
        </div>
        <Menu
          id="simple-menu"
          anchorEl={kingdomMenuAnchor}
          open={Boolean(kingdomMenuAnchor)}
          onClose={() => this.setState({ kingdomMenuAnchor: null })}
        >
          <MenuItem onClick={() => {
            this.setState({ kingdomMenuAnchor: null });
            deleteKingdom();
          }}>
            Delete kingdom
          </MenuItem>
        </Menu>
      </Fragment>
    )
  }
}

HeaderKingdomSection.propTypes = {
  hasKingdom: PropTypes.bool.isRequired,
  kingdom: PropTypes.object,
  deletingKingdom: PropTypes.bool.isRequired,
};

HeaderKingdomSection.defaultProps = {
  kingdom: null,
};

const mapStateToProps = state => {
  return {
    hasKingdom: hasKingdom(state),
    kingdom: getKingdom(state),
    deletingKingdom: isDeletingKingdom(state),
  };
};

export default connect(mapStateToProps, { ...appActions, ...kingdomActions })(
  HeaderKingdomSection,
);
