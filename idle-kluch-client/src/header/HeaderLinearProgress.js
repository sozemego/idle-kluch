import { LinearProgress } from "../components/LinearProgress";
import { connect } from "react-redux";
import { isFetching } from "../app/selectors";

export default connect(state => {
	return {
		isFetching: isFetching(state),
	};
}, null)(LinearProgress);