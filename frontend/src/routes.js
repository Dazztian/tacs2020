import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import Start from './views/Start'
import HomeUser from './views/HomeUser'
import HomeAdmin from './views/HomeAdmin'
import Signup from './views/Signup'
import Login from './views/Login'

import BlogOverview from "./views/BlogOverview";
import UserProfileLite from "./views/UserProfileLite";
import AddNewPost from "./views/AddNewPost";
import Errors from "./views/Errors";
import ComponentsOverview from "./views/ComponentsOverview";
import Tables from "./views/Tables";
import BlogPosts from "./views/BlogPosts";

export default [
  {
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/blog-overview" />
  },
  {
    path: "/blog-overview",
    exact: true,
    layout: DefaultLayout,
    component: BlogOverview
  },
  {
    path: "/user-profile-lite",
    layout: DefaultLayout,
    component: UserProfileLite
  },
  {
    path: "/add-new-post",
    layout: DefaultLayout,
    component: AddNewPost
  },
  {
    path: "/errors",
    layout: DefaultLayout,
    component: Errors
  },
  {
    path: "/components-overview",
    layout: DefaultLayout,
    component: ComponentsOverview
  },
  {
    path: "/tables",
    layout: DefaultLayout,
    component: Tables
  },
  {
    path: "/blog-posts",
    layout: DefaultLayout,
    component: BlogPosts
  },
  {
    path: "/login",
    layout: DefaultLayout,
    component: Login
  },
  {
    path: "/signup",
    layout: DefaultLayout,
    component: Signup
  },
  {
    path: "/user",
    layout: DefaultLayout,
    component: HomeUser
  },
  {
    path: "/user/lists",
    exact: true,
    layout: DefaultLayout,
    component: HomeUser
  },
  {
    path: "/admin",
    layout: DefaultLayout,
    component: HomeAdmin
  },
  {
    path: "/admin/stats",
    exact: true,
    layout: DefaultLayout,
    component: HomeAdmin
  }
];
