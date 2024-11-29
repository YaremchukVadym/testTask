import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        } else if (storage.containsKey(document.getId())) {
            Document existingDoc = storage.get(document.getId());
            document.setCreated(existingDoc.getCreated());
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc ->
                        (request.getTitlePrefixes() == null || request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix))) &&
                                (request.getContainsContents() == null || request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content))) &&
                                (request.getAuthorIds() == null || request.getAuthorIds().contains(doc.getAuthor().getId())) &&
                                (request.getCreatedFrom() == null || !doc.getCreated().isBefore(request.getCreatedFrom())) &&
                                (request.getCreatedTo() == null || !doc.getCreated().isAfter(request.getCreatedTo()))
                )
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
