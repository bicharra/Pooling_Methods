library(tidyverse)
library(ggplot2)

rm(list = ls())
baseDirectory <- "/Users/User/Desktop/Codigos/pooling-covid19/results/sensitivity/"

colSpec <- cols(id = col_character(), pop = col_double(), errors = col_double(), trials = col_double());
data <- read_tsv(paste0(baseDirectory, "results-real-dataset.csv"), col_types=colSpec);

data %>% 
  group_by(id, pop) %>%
  summarize(economy = 100 - mean(trials) * 100 / mean(pop)) %>%
  select(id, economy);

140 * (1 - 0.58) + 41
1 - 100 / 181
