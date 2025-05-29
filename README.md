# JellyShoal

> A Multi-Jellyfin Client

JellyShoal is a (relatively basic) Jellyfin client, allowing you to access content from multiple JellyFin servers at once. It is planned to have multiple servers merge metadata and potential media sources for each file, allowing you to search through all servers at once and pick whichever one has the movie you want (in the quality you want).

It is written in JB compose multiplatform, but is targeting only regular desktop now (primarily windows and linux, i dont have a mac to test on). There is an android source set, but it is an afterthought (and fails to even open right now) that may be made functional at a later point in time.

This is my first bigger project with compose, as well as jellyfin (my only other project being https://github.com/nea89o/JCoverXtremePro, an importer for mediux posters using the jellyfin ui). There might be some weirdness associated with me being relatively inexperienced in this domain.

Currently, the only video backend is VLC, which is required to be installed, but alternative rendering backends are planned for the future.
