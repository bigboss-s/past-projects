import { Field, Int, ObjectType } from "@nestjs/graphql";
import { QuestionType } from "../questions.entity";
import { ShowAnswerDTO } from "../../answers/dto/show-answer.type";

@ObjectType()
export class ShowQuestionDTO {
    @Field(type => Int)
    id: number;

    @Field()
    questionString: string;

    @Field(type => QuestionType)
    type: QuestionType;

    @Field(type => [ShowAnswerDTO], {nullable: 'itemsAndList'})
    answers?: ShowAnswerDTO[];
}