package com.utn.tacs.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

fun getLogger(): Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

