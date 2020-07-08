library(tidyverse)
library(ggplot2)

rm(list = ls())
baseDirectory <- "/Users/User/Desktop/Codigos/pooling-covid19/results/sensitivity/"
individuals <- 1000

colBaseline <- cols(id = col_character(), prev = col_double(), errors = col_double(), trials = col_double());
dataBaseline <- read_tsv(paste0(baseDirectory, "results-baseline.csv"), col_types=colBaseline);

dataBaseline %>% 
  mutate(prev = prev / 1000) %>%
  group_by(id, prev) %>%
  summarize(economy = 100 - mean(trials) * 100 / individuals) %>%
  spread(prev, economy);

dataBaseline %>% 
  mutate(prev = prev / 1000) %>%
  group_by(id, prev) %>%
  summarize(economy = 100 - mean(trials) * 100 / individuals) %>%
  group_by(prev) %>%
  summarize(max = max(economy)) %>%
  spread(prev, max);

