import { Field, Int, ObjectType } from "@nestjs/graphql";
import { ShowQuestionDTO } from "../../questions/dto/show-question.type";

@ObjectType()
export class ShowQuizDTO {
    @Field(type => Int)
    id: number;

    @Field()
    name: string;

    @Field()
    description: string;

    @Field(type => [ShowQuestionDTO])
    questions: ShowQuestionDTO[];
}