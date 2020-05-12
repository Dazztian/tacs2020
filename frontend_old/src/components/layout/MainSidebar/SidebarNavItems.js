import React from "react";
import { Nav } from "shards-react";

import SidebarNavItem from "./SidebarNavItem";

class SidebarNavItems extends React.Component {
  constructor(props) {
    super(props)


    this.onChange = this.onChange.bind(this);
  }

  componentDidMount() {

  }

  componentWillUnmount() {

  }

  onChange() {
    this.setState({
      ...this.state,
    });
  }

  render() {
    const { navItems: items } = this.state;
    return (
      <div className="nav-wrapper">
        <Nav className="nav--no-borders flex-column">
          {items.map((item, idx) => (
            <SidebarNavItem key={idx} item={item} />
          ))}
        </Nav>
      </div>
    )
  }
}

export default SidebarNavItems;
