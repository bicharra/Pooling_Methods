library(tidyverse)
library(ggplot2)

rm(list = ls())
baseDirectory <- "/Users/User/Desktop/Codigos/pooling-covid19/results/sensitivity/"
individuals <- 1000

colSpecSensitivity <- cols(id = col_character(), 
                           spec = col_double(), 
                           sens = col_double(), 
                           prev = col_double(), 
                           errors = col_double(), 
                           trials = col_double());

dataSensitivity <- read_tsv(paste0(baseDirectory, "results-sensitivity.csv"), col_types=colSpecSensitivity) %>%
  mutate(prev = prev / 1000);

View(dataSensitivity %>% 
  group_by(id, spec, sens, prev) %>%
  summarize(economy = 100 - mean(trials) * 100 / individuals) %>%
  select(id, prev, spec, sens, economy) %>%
  spread(sens, economy));
