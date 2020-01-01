# Astraria-Simulation-Generator
GUI tool to generate N-Body particle simulations for the Astraria pre-generated module (https://github.com/DavidR86/Astraria).
## Feautures:
* Single and Multithread algorithm, support for N number of threads and automatic selection depending on processor
* Manual and automatic backup of simulation progress, and recovery (recovery of backed-up simulation tool)
* Multiple numerical integrators (Currently Euler 1st order and Velocity Verlet 2nd order)
* Optional gravitational smoothing paramter to prevent same-place singularities
* Support for multiple initial condition formats, easy to add new ones
* Independent, multithread GUI (change simulation parameters through GUI, progress bars and duration estimations)
