package uk.co.jasonmarston.gateway.adaptor.input;

import uk.co.jasonmarston.gateway.valueobject.Destination;

class DestinationBuilder {
    private static final String EMPTY = "";
    private static final String PATH_SEPARATOR = "/";
    private static final String QUERY_SEPARATOR = "?";

    public static Destination buildDestination(
        final String apiRoot,
        final String path,
        final String query
    ) {
        final String[] pathSegments = path.split(PATH_SEPARATOR);
        final String subUrl = getSubUrl(path, pathSegments.length);
        final StringBuilder urlBuilder = new StringBuilder()
            .append(PATH_SEPARATOR)
            .append(apiRoot);

        if(!subUrl.isEmpty()) {
            urlBuilder
                .append(PATH_SEPARATOR)
                .append(subUrl);
        }

        if(null != query && !query.isEmpty()) {
            urlBuilder
                .append(QUERY_SEPARATOR)
                .append(query);
        }
        return Destination
            .builder()
            .hostName(pathSegments[0])
            .path(urlBuilder.toString())
            .build();
    }

    private static String getSubUrl(
        final String path,
        final int numSegments
    ) {
        if(numSegments == 1) {
            return EMPTY;
        }
        return path.substring(path.indexOf(PATH_SEPARATOR) + 1);
    }
}
