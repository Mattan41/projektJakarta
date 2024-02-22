package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public record Movies(List<MovieDto> movieDtos, LocalDateTime updated){}
