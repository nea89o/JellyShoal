{
  inputs.nixpkgs.url = "github:nixos/nixpkgs/fa48cc4e95901459c1eb5b8e31387897ddceee5d";
  inputs.flake-utils.url = "github:numtide/flake-utils";
  outputs = {
    nixpkgs,
    flake-utils,
    ...
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      overlays = [];
      pkgs = import nixpkgs {inherit system overlays;};
    in rec {
      formatter = pkgs.alejandra;
      packages = {
        libvlc = pkgs.vlc;
        x11 = pkgs.xorg.libX11;
      };
      devShells.default = pkgs.mkShell {
        VLC_LIBRARY_PATH = pkgs.lib.makeLibraryPath [packages.libvlc packages.x11];
      };
    });
}
