{
  inputs.nixpkgs.url = "github:nixos/nixpkgs/fa48cc4e95901459c1eb5b8e31387897ddceee5d";
  inputs.flake-utils.url = "github:numtide/flake-utils";
  outputs = {
    nixpkgs,
    flake-utils,
    ...
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      overlays = [
        (prev: final: {
        })
      ];
      pkgs = import nixpkgs {
        inherit system overlays;
        config.android_sdk.accept_license = true;
        config.allowUnfree = true;
      };
      androidComposition = pkgs.androidenv.composeAndroidPackages {
        #   build-tools;35.0.0 Android SDK Build-Tools 35
        # platforms;android-35 Android SDK Platform 35

        includeNDK = false;
        platformVersions = ["35"];
        buildToolsVersions = ["35.0.0"];
        abiVersions = ["x86_64" "armeabi-v7a"];
      };
    in rec {
      formatter = pkgs.alejandra;
      packages = {
        libvlc = pkgs.vlc;
        x11 = pkgs.xorg.libX11;
      };
      devShells.default = pkgs.mkShell {
        buildInputs = [
          pkgs.android-tools
          androidComposition.androidsdk
        ];

        ANDROID_HOME = "${androidComposition.androidsdk}";
        VLC_LIBRARY_PATH = pkgs.lib.makeLibraryPath [packages.libvlc packages.x11];
      };
    });
}
