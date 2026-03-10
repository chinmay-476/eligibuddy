package com.example.demo.ai;

import java.io.Serializable;

public record ChatTurn(String role, String content) implements Serializable {
}
