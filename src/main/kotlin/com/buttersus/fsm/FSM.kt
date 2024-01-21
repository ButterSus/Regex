package com.buttersus.fsm

interface FSM {
    // If `𝜔` is in `𝕄`, return true
    operator fun contains(`𝜔`: String): Boolean
}