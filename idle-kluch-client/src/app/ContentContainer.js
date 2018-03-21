import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {connect} from "react-redux";

class ContentContainer extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                HERE BE GAME OR SOMETHING ELSE
            </div>
        );
    }

}

const mapStateToProps = (state) => {
    return {
        // hasKingdom: getHasKingdom(state),
    };
};

export default connect(mapStateToProps, {})(ContentContainer);